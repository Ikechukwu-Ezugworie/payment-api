This is a simple application that acts as a middleware between a payment gateway and an end system.

# **Supported payment gateways** 

<br>

1) Interswitch paydirect
2) Interswitch webpay


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

# **IntelliJ configuration**

<br>

Paste the below configuration in run manager component tag in ``` .idea/workspace.xml ``` with the below configuration to gain access to model installation configs

```
    <configuration name="[install model]" type="CompoundRunConfigurationType">
      <toRun name="[install common repo]" type="MavenRunConfiguration" />
      <toRun name="[install common]" type="MavenRunConfiguration" />
      <toRun name="[install core repo]" type="MavenRunConfiguration" />
      <toRun name="[install core]" type="MavenRunConfiguration" />
      <method v="2" />
    </configuration>
    <configuration name="[clean install]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="clean" />
                <option value="install" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[clean]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="clean" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[install common repo]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="install:install-file" />
                <option value="-Durl=file:///." />
                <option value="-Dfile=./repo/payment-common-1.0-SNAPSHOT.jar" />
                <option value="-DgroupId=com.bw.payment" />
                <option value="-DartifactId=payment-common" />
                <option value="-Dpackaging=jar" />
                <option value="-Dversion=1.0-SNAPSHOT" />
                <option value="-DlocalRepositoryPath=./repo" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[install common]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="install:install-file" />
                <option value="-Durl=file:///." />
                <option value="-Dfile=./repo/payment-common-1.0-SNAPSHOT.jar" />
                <option value="-DgroupId=com.bw.payment" />
                <option value="-DartifactId=payment-common" />
                <option value="-Dpackaging=jar" />
                <option value="-Dversion=1.0-SNAPSHOT" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[install core repo]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="install:install-file" />
                <option value="-Durl=file:///." />
                <option value="-Dfile=./repo/payment-core-1.0-SNAPSHOT.jar" />
                <option value="-DgroupId=com.bw.payment" />
                <option value="-DartifactId=payment-core" />
                <option value="-Dpackaging=jar" />
                <option value="-Dversion=1.0-SNAPSHOT" />
                <option value="-DlocalRepositoryPath=./repo" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[install core]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="install:install-file" />
                <option value="-Durl=file:///." />
                <option value="-Dfile=./repo/payment-core-1.0-SNAPSHOT.jar" />
                <option value="-DgroupId=com.bw.payment" />
                <option value="-DartifactId=payment-core" />
                <option value="-Dpackaging=jar" />
                <option value="-Dversion=1.0-SNAPSHOT" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[model:clean]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="clean" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$/../bw-payment-dsl" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2" />
    </configuration>
    <configuration name="[model:package]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="package" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$/../bw-payment-dsl" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2">
        <option name="RunConfigurationTask" enabled="true" run_configuration_name="[model:clean]" run_configuration_type="MavenRunConfiguration" />
      </method>
    </configuration>
    <configuration name="[ninja:run]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings">
          <MavenRunnerSettings>
            <option name="delegateBuildToMaven" value="false" />
            <option name="environmentProperties">
              <map />
            </option>
            <option name="jreName" value="#USE_PROJECT_JDK" />
            <option name="mavenProperties">
              <map>
                <entry key="%dev.bw.payment.db.username" value="'test'" />
              </map>
            </option>
            <option name="passParentEnv" value="true" />
            <option name="runMavenInBackground" value="true" />
            <option name="skipTests" value="false" />
            <option name="vmOptions" value="" />
          </MavenRunnerSettings>
        </option>
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="ninja:run" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2">
        <option name="MakeProject" enabled="true" />
      </method>
    </configuration>
    <configuration name="[package]" type="MavenRunConfiguration" factoryName="Maven">
      <MavenSettings>
        <option name="myGeneralSettings" />
        <option name="myRunnerSettings" />
        <option name="myRunnerParameters">
          <MavenRunnerParameters>
            <option name="profiles">
              <set />
            </option>
            <option name="goals">
              <list>
                <option value="package" />
              </list>
            </option>
            <option name="pomFileName" />
            <option name="profilesMap">
              <map />
            </option>
            <option name="resolveToWorkspace" value="false" />
            <option name="workingDirPath" value="$PROJECT_DIR$" />
          </MavenRunnerParameters>
        </option>
      </MavenSettings>
      <method v="2">
        <option name="RunConfigurationTask" enabled="true" run_configuration_name="[clean]" run_configuration_type="MavenRunConfiguration" />
      </method>
    </configuration>
    <list>
      <item itemvalue="Compound.[install model]" />
      <item itemvalue="Maven.[package]" />
      <item itemvalue="Maven.[model:package]" />
      <item itemvalue="Maven.[install common repo]" />
      <item itemvalue="Maven.[model:clean]" />
      <item itemvalue="Maven.[clean install]" />
      <item itemvalue="Maven.[clean]" />
      <item itemvalue="Maven.[install common]" />
      <item itemvalue="Maven.[install core]" />
      <item itemvalue="Maven.[install core repo]" />
      <item itemvalue="Maven.[ninja:run]" />
    </list>
  ```