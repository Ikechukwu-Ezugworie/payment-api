This is a simple application that acts as a middleware between a payment gateway and an end system.

# **Supported payment gateways** 

1) Interswitch paydirect


# **Release history**

<br>

**4th December 2018**

* Added monitoring endpoint for ensuring communication with endsystem. <br>
```
{base-url}/payments/interswitch/paydirect/monitor
```
* Added support for setting DB credentials via java variables using tomcat service configuration. <br><br>
Here's the list of supported java variables<br><br>
    1) bw.payment.db.url
    1) bw.payment.db.username
    1) bw.payment.db.password

```
Environment='... -Dbw.payment.db.url={full DB JDBC URL} -Dbw.payment.db.username={dbusername} -Dbw.payment.db.password={dbpassword}'
```
<br>

**20th October 2018**

* Fixed issue with duplicate notification