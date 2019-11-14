package example.sentryUtils;

import org.apache.spark.{TaskEndReason, ExceptionFailure};
import org.apache.spark.scheduler._;
import org.apache.spark.internal.Logging;

import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;

class SentryListener extends SparkListener with Logging {
  override def onApplicationStart(
      applicationStart: SparkListenerApplicationStart
  ) {
    Sentry.init()

    Sentry
      .getContext()
      .recordBreadcrumb(
        new BreadcrumbBuilder()
          .setMessage(s"Application ${applicationStart.appName} started")
          .withData("time", applicationStart.time.toString)
          .withData("appAttemptId", applicationStart.appAttemptId.getOrElse(""))
          .build()
      );
    logInfo("RECORDED BREADCRUMBS");
  }

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd) {
    Sentry
      .getContext()
      .recordBreadcrumb(
        new BreadcrumbBuilder()
          .setMessage("Application ended")
          .withData("time", applicationEnd.time.toString)
          .build()
      );
  }

  override def onJobStart(jobStart: SparkListenerJobStart) {
    Sentry
      .getContext()
      .recordBreadcrumb(
        new BreadcrumbBuilder()
          .setMessage(s"Job ${jobStart.jobId} Started")
          .withData("time", jobStart.time.toString)
          .build()
      );
  }

  override def onTaskEnd(taskEnd: SparkListenerTaskEnd) {
    TaskEndParser.parseTaskEndReason(taskEnd.reason)

    if (taskEnd.taskInfo.failed) {
      logInfo(taskEnd.reason.toString);
    }
  }
}

object TaskEndParser {
  def parseTaskEndReason(reason: TaskEndReason) {
    // It would fail on the following inputs: ExecutorLostFailure(_, _, _), FetchFailed(_, _, _, _, _), Resubmitted, Success, TaskCommitDenied(_, _, _), TaskKilled(_, _, _), TaskResultLost, UnknownReason
    reason match {
      case ExceptionFailure(_,_,_,_,_,_,_) => this.sendExceptionFailureToSentry(reason.asInstanceOf[ExceptionFailure])
    }
  }

  def sendExceptionFailureToSentry(reason: ExceptionFailure) {
    Sentry.getContext().addTag("className", reason.className);
    Sentry.getContext().addExtra("description", reason.description);

    reason.exception match {
      case Some(exception) => Sentry.capture(exception)
      case None => {
        val throwable: Throwable = new Throwable(reason.description);
        throwable.setStackTrace(reason.stackTrace)
        Sentry.capture(throwable)
      }
    }
  }
}
