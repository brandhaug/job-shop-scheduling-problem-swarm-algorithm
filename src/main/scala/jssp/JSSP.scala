package jssp

import jssp.AlgorithmEnum._
import swarm.ba.{BA, Bee}
import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
case class State(particles: Seq[Particle],
                 generation: Int = 0,
                 bestSchedule: Seq[OperationTimeSlot],
                 bestMakeSpan: Int)
class JSSP(jobs: Seq[Job],
           machines: Seq[Machine],
           algorithmEnum: AlgorithmEnum) {

  val pso: PSO = PSO(jobs, machines)
  val ba: BA = BA(jobs, machines)

  def tick(previousState: Option[State] = None): State = {
    algorithmEnum match {
      case ParticleSwarmOptimization =>
        previousState match {
          case None =>
            // No previous state => initialize first state
            val (particles, bestSchedule, bestMakeSpan) = pso.initializePopulation()
            State(particles = particles,
                  generation = 1,
                  bestSchedule = bestSchedule,
                  bestMakeSpan = bestMakeSpan)

          case Some(prevState) =>
            val (particles,
            bestSchedule: Seq[OperationTimeSlot],
            bestMakeSpan: Int) = pso.tick(prevState.particles)
            State(particles, prevState.generation + 1, bestSchedule, bestMakeSpan)
        }

      case BeesAlgorithm =>
        // Do same with bees, but also consider handling the "transition to next state" within the 'ba' and 'pso' or at least in separate functions
      case None =>
        // No previous state => initialize first state
        val (bees, bestSchedule, bestMakeSpan) = pso.initializePopulation()
        State(bees = bees,
              generation = 1,
              bestSchedule = bestSchedule,
              bestMakeSpan = bestMakeSpan)

      case Some(prevState) =>
        val (bees,
        bestSchedule: Seq[OperationTimeSlot],
        bestMakeSpan: Int) = ba.tick(prevState.bees)
        State(bees, prevState.generation + 1, bestSchedule, bestMakeSpan)

    }
  }
}

object JSSP {
  val minOperationWeight: Double = 0.0
  val maxOperationWeight: Double = 2.0
  val minVelocity: Double = -2.0
  val maxVelocity: Double = 2.0
}
