package com.scgexpress.backoffice.android.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.util.IOUtils
import com.scgexpress.backoffice.android.aws.DeveloperAuthenticationProvider
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.io.IOException


class FileUploader(
        context: Context,
        loginRepository: LoginRepository,
        loginPreference: LoginPreference
) {

    private val s3Client by lazy {
        val developerProvider = DeveloperAuthenticationProvider(loginRepository, loginPreference, Const.AWS_POOL_ID, Regions.AP_SOUTHEAST_1)
        val credentialsProvider = CognitoCachingCredentialsProvider(
                context,
                developerProvider,
                Regions.AP_SOUTHEAST_1
        )
        AmazonS3Client(credentialsProvider)
    }

    private val transferUtility by lazy {
        TransferUtility.builder()
                .context(context)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3Client)
                .build()
    }

    fun uploadFile(file: File, uploadPath: String): Single<String?> {
        return uploadFile(transferUtility, file, uploadPath)
    }

//    fun test(){
//        s3Client.endpoint = "pickup_parcel/120903988004/1568344027163-1543-sender.jpg" //endpoint
//        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1))
//
//        val expires = Date(Date().time + 1000 * 60 * 60) // 1 minute to expire
//        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(Const.AWS_BUCKET, Const.AWS_SECRET_KEY)  //generating the signatured url
//            .apply {
//                expiration = expires
//            }
//        val url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
//        Timber.d("generatePresignedUrlRequest ${url}")
//    }

    private fun S3ObjectInputStream.toBitmap(): Single<Bitmap> {
        return Single.just(IOUtils.toByteArray(this))
            .flatMap { result ->
                val bitmap = BitmapFactory.decodeByteArray(result, 0, result.size)
                this.close()
                Single.just(bitmap)
            }
    }

    fun downloadBitmap(fullPathFile: String): Single<Bitmap> {
        val split = fullPathFile.split("/")
        require(split.size >= 2) { "filepath must start with bucket name (\$bucket-name/\$path)" }
        val bucket = split[0]
        val path = split.drop(1).joinToString("/")
        return downloadBitmap(bucket, path)
    }

    fun downloadBitmap(
        s3bucket: String,
        s3FilePath: String
    ): Single<Bitmap> {
        return Single.fromCallable {
            s3Client.getObject(GetObjectRequest(s3bucket, s3FilePath))
        }.flatMap {
            it.objectContent.toBitmap()
        }
    }

    companion object {
        fun uploadFile(transferUtility: TransferUtility, file: File, uploadPath: String): Single<String?> {
            return Single.create { emitter ->
                transferUtility.upload(uploadPath, file).setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        if (TransferState.COMPLETED == state) {
                            val fileAddress = "${Const.AWS_BUCKET}/$uploadPath"
//                            val url = "https://${Const.AWS_BUCKET}.s3.amazonaws.com/$uploadPath"
//                            val u = s3Client.getResourceUrl(Const.AWS_BUCKET, uploadPath)
                            Timber.d("S3 url fileAddress: $fileAddress u")
                            emitter.onSuccess(fileAddress)
                        } else if (TransferState.FAILED == state) {
                            emitter.onError(IOException("cannot upload ${file.absoluteFile} to ${Const.AWS_BUCKET}::$uploadPath"))
                        }
                    }

                    override fun onError(id: Int, ex: Exception) {
                        ex.printStackTrace()
                        emitter.onError(ex)
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        //val percentDone = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    }
                })
            }
        }
    }
}