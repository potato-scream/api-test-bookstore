package config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/credentials.properties"})
public interface CredentialsConfig extends Config {

  @Key("getLogin")
  String login();

  @Key("getPassword")
  String password();
}
