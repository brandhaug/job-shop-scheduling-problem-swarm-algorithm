package jssp

import swarm.ba.Bee
import swarm.pso.Particle

case class State(population: Either[Seq[Particle], Seq[Bee]], generation: Int = 0, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int)
