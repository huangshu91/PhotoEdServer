package com.photoed.server.util;

import java.util.logging.Logger;

public class Log {
  private static final String Tag = "socalLog";
  private static final Logger log = Logger.getLogger(Tag);

  // informational output, no problems
  public static final void i(String msg) {
    log.info(msg);
  }

  // informational output, warning

  // informational output, error

}
