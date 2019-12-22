#!groovy

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import jenkins.model.Jenkins

println("Setting credentials")

def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

def artifactoryPassword = new File("/run/secrets/artifactoryPassword").text.trim()

def credentials = ['username': 'admin', 'password': artifactoryPassword, 'description': 'Irtifactory OSS Credentials']
def usernamePasswordCred = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, 'artifactoryCredentials',
        credentials.description, credentials.username, credentials.password)

store.addCredentials(domain, usernamePasswordCred)