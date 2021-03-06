package skinny.micro.contrib

import skinny.micro.SkinnyMicroBase
import skinny.micro.async.AsyncBeforeAfterDsl
import skinny.micro.context.SkinnyContext
import skinny.micro.contrib.csrf.CSRFTokenGenerator

/**
 * Provides cross-site request forgery protection.
 *
 * If a request is determined to be forged, the `handleForgery()` hook is invoked.
 * Otherwise, a token for the next request is prepared with `prepareCsrfToken`.
 */
trait AsyncCSRFTokenSupport { this: SkinnyMicroBase with AsyncBeforeAfterDsl =>

  before(isForged(context)) { implicit ctx => handleForgery()(ctx) }
  before() { implicit ctx => prepareCsrfToken() }

  /**
   * Tests whether a request with a unsafe method is a potential cross-site
   * forgery.
   *
   * @return true if the request is an unsafe method (POST, PUT, DELETE, TRACE,
   * CONNECT, PATCH) and the request parameter at `csrfKey` does not match
   * the session key of the same name.
   */
  protected def isForged(implicit ctx: SkinnyContext): Boolean = {
    implicit val ctx = context
    !request.requestMethod.isSafe &&
      session.get(csrfKey) != params.get(csrfKey) &&
      !CSRFTokenSupport.HeaderNames.map(request.headers.get).contains(session.get(csrfKey))
  }

  /**
   * Take an action when a forgery is detected. The default action
   * halts further request processing and returns a 403 HTTP status code.
   */
  protected def handleForgery()(implicit ctx: SkinnyContext): Unit = {
    halt(403, "Request tampering detected!")
  }

  /**
   * Prepares a CSRF token.  The default implementation uses `GenerateId`
   * and stores it on the session.
   */
  // NOTE: keep return type as Any for backward compatibility
  protected def prepareCsrfToken()(implicit ctx: SkinnyContext): Any = {
    session.getOrElseUpdate(csrfKey, CSRFTokenGenerator.apply()).toString
  }

  /**
   * The key used to store the token on the session, as well as the parameter
   * of the request.
   */
  def csrfKey: String = CSRFTokenSupport.DefaultKey

  /**
   * Returns the token from the session.
   */
  protected[skinny] def csrfToken(implicit ctx: SkinnyContext): String =
    context.request.getSession.getAttribute(csrfKey).asInstanceOf[String]

}