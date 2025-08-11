package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/credentials.properties"})
public interface CredentialsConfig extends Config {

  @org.aeonbits.owner.Config.Key("login")
  String login();

  @org.aeonbits.owner.Config.Key("password")
  String password();
}
