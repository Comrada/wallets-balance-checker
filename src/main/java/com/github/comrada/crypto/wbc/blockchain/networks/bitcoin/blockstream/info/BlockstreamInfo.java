package com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.blockstream.info;

import com.github.comrada.crypto.wbc.blockchain.BlockchainApi;
import com.github.comrada.crypto.wbc.blockchain.exception.NetworkException;
import com.github.comrada.crypto.wbc.blockchain.networks.bitcoin.BaseHttpClient;
import java.math.BigDecimal;
import java.net.http.HttpClient;

public class BlockstreamInfo extends BaseHttpClient implements BlockchainApi {

  private static final String ADDRESS_URL = "https://blockstream.info/api/address/";

  public BlockstreamInfo(HttpClient client) {
    super(client);
  }

  @Override
  public String name() {
    return "Bitcoin";
  }

  @Override
  public BigDecimal balance(String address) {
    Response response = get(ADDRESS_URL + address, Response.class);
    if (!response.address().equals(address)) {
      throw new NetworkException(
          "Blockstream.info returned a response with a different balance for the wallet than requested.");
    }
    return response.getFinalBalance();
  }
}
