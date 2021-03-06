package uk.gov.nationalarchives.notifications.decoders

import io.circe.{Decoder, HCursor}

object ScanDecoder {

  case class ScanEvent(detail: ScanDetail) extends IncomingEvent

  case class ScanDetail(
                         repositoryName: String,
                         tags: List[String],
                         imageDigest: String,
                         findingSeverityCounts: ScanFindingCounts
                       )

  case class ScanFindingCounts(critical: Int, high: Int, medium: Int, low: Int, undefined: Int, informational: Int)

  implicit val decodeScanFindingCounts: Decoder[ScanFindingCounts] = (c: HCursor) => for {
    critical <- c.downField("CRITICAL").as[Option[Int]]
    high <- c.downField("HIGH").as[Option[Int]]
    medium <- c.downField("MEDIUM").as[Option[Int]]
    low <- c.downField("LOW").as[Option[Int]]
    undefined <- c.downField("UNDEFINED").as[Option[Int]]
    informational <- c.downField("INFORMATIONAL").as[Option[Int]]
  } yield ScanFindingCounts(
    critical.getOrElse(0),
    high.getOrElse(0),
    medium.getOrElse(0),
    low.getOrElse(0),
    undefined.getOrElse(0),
    informational.getOrElse(0)
  )

  implicit val decodeScanDetail: Decoder[ScanDetail] = (c: HCursor) => for {
    repositoryName <- c.downField("repository-name").as[String]
    imageTags <- c.downField("image-tags").as[List[String]]
    imageDigest <- c.downField("image-digest").as[String]
    findingSeverityCounts <- c.downField("finding-severity-counts").as[ScanFindingCounts]
  } yield ScanDetail(repositoryName, imageTags, imageDigest, findingSeverityCounts)

  val decodeScanEvent: Decoder[IncomingEvent] = (c: HCursor) => for {
    detail <- c.downField("detail").as[ScanDetail]
  } yield ScanEvent(detail)
}
