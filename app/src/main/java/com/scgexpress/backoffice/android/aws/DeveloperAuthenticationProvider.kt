package com.scgexpress.backoffice.android.aws

import android.annotation.SuppressLint
import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider
import com.amazonaws.regions.Regions
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import javax.inject.Inject

// Initialize any other objects needed here.
class DeveloperAuthenticationProvider @Inject
constructor(private val loginRepository: LoginRepository, private val loginPreference: LoginPreference,
            identityPoolId: String, region: Regions)
    : AWSAbstractCognitoDeveloperIdentityProvider(null, identityPoolId, region) {

    companion object {
        private const val developerProvider = "login.scgexpress.c2"
    }

    // Return the developer provider name which you choose while setting up the
    // identity pool in the &COG; Console

    override fun getProviderName(): String {
        return developerProvider
    }

    // Use the refresh method to communicate with your backend to get an
    // identityId and token.

    @SuppressLint("CheckResult")
    override fun refresh(): String {

        // Override the existing token
        token = loginPreference.token
        if (token == "")
            setToken(null)
        else setToken(token)

        // Get the identityId and token by making a call to your backend
        // (Call to your backend)
        val call = loginRepository.getIdentity()
        val result = call.execute().body()

        identityId = result!!.identityId
        token = result.token
        loginPreference.identityID = identityId
        loginPreference.token = token
        update(identityId, token)

        // Call the update method with updated identityId and token to make sure
        // these are ready to be used from Credentials Provider.
        return token
    }

    // If the app has a valid identityId return it, otherwise get a valid
    // identityId from your backend.

    @SuppressLint("CheckResult")
    override fun getIdentityId(): String {
        // Load the identityId from the cache
        identityId = loginPreference.identityID

        if (identityId == null) {
            val call = loginRepository.getIdentity()
            val result = call.execute().body()
            identityId = result!!.identityId
            token = result.token
        }
        loginPreference.identityID = identityId
        loginPreference.token = token
        return identityId
    }
}