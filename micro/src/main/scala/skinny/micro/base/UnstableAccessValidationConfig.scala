package skinny.micro.base

/**
 * Configures to activate unstable access validation or not.
 */
trait UnstableAccessValidationConfig {

  /**
   * Enables unstable access validation.
   */
  protected def unstableAccessValidationEnabled: Boolean = true

  /**
   * Enables mostly stable Servlet HttpSession implementation instead.
   */
  protected def useMostlyStableHttpSession: Boolean = false

}
