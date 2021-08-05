package org.apache.hadoop.hdfs.client.impl;

import static org.junit.Assert.assertEquals;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys.Retry;
import org.apache.hadoop.hdfs.client.impl.DfsClientConf.FetchBlockLocationsRetryer;
import org.junit.Test;

public class TestFetchBlockLocationsRetryer {

  private static final double EPSILON = 0.001;

  @Test
  public void testDefaultRetryPolicy() {
    Configuration conf = new Configuration();
    int base = Retry.WINDOW_BASE_DEFAULT;
    int multiplier = Retry.WINDOW_MULTIPLIER_DEFAULT;

    conf.setInt(Retry.WINDOW_BASE_KEY, base);
    conf.setInt(Retry.WINDOW_MULTIPLIER_KEY, multiplier);

    // disable random factor so it's easier to test
    FetchBlockLocationsRetryer retryer = new FetchBlockLocationsRetryer(conf, false);

    // we've disabled the random factor, so the wait times here would be the
    // worst case scenarios
    assertEquals(3_000, retryer.getWaitTime(0), EPSILON);
    assertEquals(3_000 + 6_000, retryer.getWaitTime(1), EPSILON);
    assertEquals(6_000 + 9_000, retryer.getWaitTime(2), EPSILON);
    assertEquals(9_000 + 12_000, retryer.getWaitTime(3), EPSILON);
  }

  @Test
  public void testAggressiveRetryPolicy() {
    Configuration conf = new Configuration();
    int base = 10;
    int multiplier = 5;

    conf.setInt(Retry.WINDOW_BASE_KEY, base);
    conf.setInt(Retry.WINDOW_MULTIPLIER_KEY, multiplier);

    // disable random factor so it's easier to test
    FetchBlockLocationsRetryer retryer = new FetchBlockLocationsRetryer(conf, false);

    // we've disabled the random factor, so the wait times here would be the
    // worst case scenarios
    assertEquals(50, retryer.getWaitTime(0), EPSILON);
    assertEquals(50 + 500, retryer.getWaitTime(1), EPSILON);
    assertEquals(500 + 3_750, retryer.getWaitTime(2), EPSILON);
    assertEquals(3_750 + 25_000, retryer.getWaitTime(3), EPSILON);
  }

  @Test
  public void testMaxWindowSize() {
    Configuration conf = new Configuration();
    int base = 10;
    int multiplier = 10;
    int maxWindow = 1_000;

    conf.setInt(Retry.WINDOW_BASE_KEY, base);
    conf.setInt(Retry.WINDOW_MULTIPLIER_KEY, multiplier);
    conf.setInt(Retry.WINDOW_MAXIMUM_KEY, maxWindow);

    // disable random factor so it's easier to test
    FetchBlockLocationsRetryer retryer = new FetchBlockLocationsRetryer(conf, false);

    // we've disabled the random factor, so the wait times here would be the
    // worst case scenarios
    assertEquals(100, retryer.getWaitTime(0), EPSILON);
    assertEquals(100 + 1_000, retryer.getWaitTime(1), EPSILON);
    assertEquals(1_000 + 1_000, retryer.getWaitTime(2), EPSILON);
    assertEquals(1_000 + 1_000, retryer.getWaitTime(3), EPSILON);
  }
}
