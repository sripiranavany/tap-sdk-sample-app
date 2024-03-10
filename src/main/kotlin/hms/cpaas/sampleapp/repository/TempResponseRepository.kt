package hms.cpaas.sampleapp.repository

import hms.cpaas.sampleapp.model.TempResponse
import org.springframework.data.jpa.repository.JpaRepository

interface TempResponseRepository: JpaRepository<TempResponse, Long> {
    fun findBySubscriberId(subscriberId: String): TempResponse?
}