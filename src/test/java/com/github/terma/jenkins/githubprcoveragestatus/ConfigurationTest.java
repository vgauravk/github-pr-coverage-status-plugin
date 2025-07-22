package com.github.terma.jenkins.githubprcoveragestatus;

import com.github.terma.jenkins.githubprcoveragestatus.Configuration;
import com.github.terma.jenkins.githubprcoveragestatus.Configuration.ConfigurationDescriptor;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class ConfigurationTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void secretsStoredAsSecretAndExposedAsPlainViaGetter() throws Exception {
        String ghToken = "ghp_testsecret";
        String sonarToken = "sonar_token_secret";
        String sonarPassword = "sonar_pwd_secret";

        ConfigurationDescriptor config = new ConfigurationDescriptor();

        // Set fields via reflection, as Jenkins global config binds via data-binding, not public API
        setSecretField(config, "personalAccessToken", ghToken);
        setSecretField(config, "sonarToken", sonarToken);
        setSecretField(config, "sonarPassword", sonarPassword);

        assertEquals(ghToken, config.getPersonalAccessToken());
        assertEquals(sonarToken, config.getSonarToken());
        assertEquals(sonarPassword, config.getSonarPassword());

        Secret ghTokenSecret = getSecretField(config, "personalAccessToken");
        Secret sonarTokenSecret = getSecretField(config, "sonarToken");
        Secret sonarPwdSecret = getSecretField(config, "sonarPassword");

        assertNotNull(ghTokenSecret);
        assertNotEquals(ghToken, ghTokenSecret.getEncryptedValue());

        assertNotNull(sonarTokenSecret);
        assertNotEquals(sonarToken, sonarTokenSecret.getEncryptedValue());

        assertNotNull(sonarPwdSecret);
        assertNotEquals(sonarPassword, sonarPwdSecret.getEncryptedValue());
    }

    private void setSecretField(Object obj, String fieldName, String value) throws Exception {
        java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, Secret.fromString(value));
    }

    private Secret getSecretField(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        Object val = f.get(obj);
        return val instanceof Secret ? (Secret) val : null;
    }
}
