# How to use keytool utility for key management

**To generate a new private key with alias and store it in file keystore.store**
> $ keytool -genkey -alias MYALIAS -keystore keystore.store

**Generate a temporary certificate file**
> $ keytool -export -alias MYALIAS -file certfile.cer -keystore keystore.store
 
**To make a Certificate Signing Request(CSR) from existing JAVA Keystore**
> keytool -certreq -alias MYALIAS -keystore keystore.store -file MYALIAS.csr

References:
* [alvinalexander.com](http://alvinalexander.com/java/java-keytool-keystore-certificates)
* [sslshopper.com](https://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html)
