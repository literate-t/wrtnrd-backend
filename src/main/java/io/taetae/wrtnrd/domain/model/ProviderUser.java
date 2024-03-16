package io.taetae.wrtnrd.domain.model;

public interface ProviderUser {

  String getId();
  String getUsername();
  String getPassword();
  String getEmail();

  String getProvider();
}
