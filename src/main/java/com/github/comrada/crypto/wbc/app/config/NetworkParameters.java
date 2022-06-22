package com.github.comrada.crypto.wbc.app.config;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.network")
public class NetworkParameters {

  private Set<Blockchain> blockchains = new HashSet<>();
  private Map<String, Blockchain> configMap;

  public Set<Blockchain> getBlockchains() {
    return unmodifiableSet(blockchains);
  }

  public NetworkParameters() {
  }

  public void setBlockchains(Set<Blockchain> blockchains) {
    this.blockchains = blockchains;
    this.configMap = blockchains.stream().collect(toUnmodifiableMap(Blockchain::getName, identity()));
  }

  public NetworkParameters(Set<Blockchain> blockchains) {
    this.blockchains = requireNonNull(blockchains);
  }

  public Map<String, Blockchain> getConfigMap() {
    return configMap;
  }

  public static final class Blockchain implements NetworkConfig {

    private String name;
    private String asset;
    private Map<String, String> parameters = new HashMap<>();

    public Blockchain() {
    }

    public Blockchain(String name, String asset, Map<String, String> parameters) {
      this.name = requireNonNull(name);
      this.asset = requireNonNull(asset);
      this.parameters = requireNonNull(parameters);
    }

    @Override
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = requireNonNull(name);
    }

    @Override
    public String getAsset() {
      return asset;
    }

    public void setAsset(String asset) {
      this.asset = requireNonNull(asset);
    }

    @Override
    public Map<String, String> getParameters() {
      return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
      this.parameters = requireNonNull(parameters);
    }
  }
}