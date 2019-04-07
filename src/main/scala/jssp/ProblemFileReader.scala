package jssp

import java.io.InputStream

// TODO: Should this be object?
object ProblemFileReader {
  def readFile(directoryName: String, fileName: String): (Seq[Job], Seq[Machine]) = {
    val stream: InputStream = getClass.getResourceAsStream(directoryName + "/" + fileName)
    val lines: List[String] = scala.io.Source.fromInputStream(stream).getLines.toList
    val first :: rest = lines

    // Parse first line and set args
    val numberOfJobs :: numberOfMachines :: Nil = first.trim.split("\\s+").map(_.toInt).toList

    // Parse rest of file
    val jobs: Seq[Job] = for {
      (line, jobId) <- rest.zipWithIndex if jobId < numberOfJobs
    } yield {
      val jobLine = line.trim.split("\\s+").map(_.toInt)

      // create jobs from job line
      val operations: Seq[Operation] = for (j <- jobLine.indices by 2) yield {
        val machineId = jobLine(j)
        val operationDuration = jobLine(j + 1)
        Operation(machineId, jobId, operationDuration)
      }

      Job(jobId, operations)
    }

    val machines: Seq[Machine] = for (machineId <- 0 until numberOfMachines) yield {
      Machine(machineId)
    }

    (jobs, machines)
  }
}
