package jssp

import jssp.AlgorithmEnum._
import swarm.ba.BA
import swarm.pso.PSO

/**
  * Job Shop Scheduling Problem
  */
class JSSP(jobs: Seq[Job], machines: Seq[Machine], algorithmEnum: AlgorithmEnum) {
  val pso: PSO = PSO(jobs, machines)
  val ba: BA   = BA(jobs, machines)

  def tick(previousState: Option[State] = None): State = {
    algorithmEnum match {
      case ParticleSwarmOptimization => {
        previousState match {
          case None =>
            // No previous state => initialize first state
            val (particles, bestSchedule, bestMakeSpan) = pso.initializePopulation()
            State(population = Left(particles), generation = 1, bestSchedule = bestSchedule, bestMakeSpan = bestMakeSpan)

          case Some(prevState) =>
            val (particles, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int) = pso.tick(prevState.population.left.get)
            State(Left(particles), prevState.generation + 1, bestSchedule, bestMakeSpan)
        }
      }

      case BeesAlgorithm => {
        previousState match {
          case None =>
            // No previous state => initialize first state
            val (bees, bestSchedule, bestMakeSpan) = ba.initializePopulation()
            State(population = Right(bees), generation = 1, bestSchedule = bestSchedule, bestMakeSpan = bestMakeSpan)

          case Some(prevState) =>
            val (bees, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int) = ba.tick(prevState.population.right.get)
            State(Right(bees), prevState.generation + 1, bestSchedule, bestMakeSpan)
        }
      }
    }
  }
}

object JSSP {
  val minOperationWeight: Double = 0.0
  val maxOperationWeight: Double = 2.0
  val minVelocity: Double        = -2.0
  val maxVelocity: Double        = 2.0
}
