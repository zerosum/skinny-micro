package skinny.micro.multipart

import java.io.{ File, FileInputStream }
import java.nio.charset.Charset

import org.mozilla.universalchardet.UniversalDetector
import skinny.util.LoanPattern._
import org.slf4j.LoggerFactory

import scala.io.Codec

/**
 * File charset utility.
 */
object FileCharset {

  private[this] val logger = LoggerFactory.getLogger(getClass)

  def apply(file: File): Charset = {
    val buf = Array.ofDim[Byte](8192)
    val detector = new UniversalDetector(null)
    try {
      using(new FileInputStream(file)) { fis =>
        var idx = fis.read(buf)
        while (idx > 0 && !detector.isDone) {
          detector.handleData(buf, 0, idx)
          idx = fis.read(buf)
        }
        detector.dataEnd()
      }
      getCharset(detector, Codec.fileEncodingCodec)
    } catch {
      case scala.util.control.NonFatal(t) =>
        logger.warn("Failed to detect charset for file: " + file.getPath + ".", t)
        Codec.defaultCharsetCodec.charSet
    } finally {
      detector.reset()
    }
  }

  private[this] def getCharset(detector: UniversalDetector, default: Codec): Charset = {
    val cs = detector.getDetectedCharset
    if (cs == null || cs.trim().isEmpty) {
      default.charSet
    } else {
      Charset.forName(cs)
    }
  }

  def apply(barr: Array[Byte]): Charset = {
    val detector = new UniversalDetector(null)
    try {
      var idx = 0
      while (idx < barr.length && !detector.isDone) {
        if (idx > 0) detector.handleData(barr, 0, idx)
        idx += 1
      }
      detector.dataEnd()
      getCharset(detector, Codec.defaultCharsetCodec)
    } catch {
      case scala.util.control.NonFatal(t) =>
        logger.warn("Failed to detect charset.", t)
        Codec.defaultCharsetCodec.charSet
    } finally {
      detector.reset()
    }
  }

}
