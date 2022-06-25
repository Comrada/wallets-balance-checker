package com.github.comrada.crypto.wbc.app.config;


import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.BitcoinWeb;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockchain.info.BlockchainInfo;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info.BlockstreamInfo;
import com.github.comrada.crypto.wbc.blockchain.networks.ethereum.EthereumApi;
import com.github.comrada.crypto.wbc.blockchain.networks.ripple.RippleNet;
import com.github.comrada.crypto.wbc.checker.NetworkConfig;
import com.github.comrada.crypto.wbc.checker.NetworksManager;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@EnableConfigurationProperties(NetworkParameters.class)
public class NetworksConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(NetworksConfig.class);

  @Bean
  NetworksManager networksManager(List<BlockchainApi> apis, NetworkParameters parameters) {
    Set<String> enabledNetworks = parameters.getEnabledNetworks();
    Map<String, BlockchainApi> mappedApis = apis.stream()
        .filter(api -> enabledNetworks.contains(api.name()))
        .collect(toMap(BlockchainApi::name, identity()));
    LOGGER.info("Balance checkers for assets {} have been registered", mappedApis.keySet());
    return new NetworksManager(mappedApis);
  }

  @Bean
  BlockchainApi ethereumBalance(NetworkParameters parameters) {
    NetworkConfig ethereumConfig = parameters.getConfigFor("Ethereum");
    return new EthereumApi(ethereumConfig);
  }

  @Bean
  BlockchainApi rippleBalance(NetworkParameters parameters) {
    NetworkConfig rippleNetConfig = parameters.getConfigFor("Ripple");
    return new RippleNet(rippleNetConfig);
  }

  @Bean
  BlockchainApi bitcoinWebServicesBalancer() {
    HttpClient client = HttpClient.newBuilder()
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();
    BlockstreamInfo blockstreamInfo = new BlockstreamInfo(client);
    BlockchainInfo blockchainInfo = new BlockchainInfo(client);
    return new BitcoinWeb(List.of(blockstreamInfo, blockchainInfo));
  }
}
