package it.polito.mad.courtreservationapp.utils

import it.polito.mad.courtreservationapp.models.Service

object ServiceUtils {
    private val services: Map<Long, Service> = mapOf(
        Pair(0L, Service("Showers", 0L)),
        Pair(1L, Service("Equipment", 1L)),
        Pair(2L, Service("Couch",2L)),
        Pair(3L, Service("Refreshment",3L)),
        Pair(4L, Service("Wifi",4L)),
        Pair(5L, Service("Lockers",5L))
    )

    fun getServices(list: List<*>): List<Service>{
        val services = mutableListOf<Service>()
        for(item in list){
            val ser = item as HashMap<*, *>
            val description = (ser["description"] as String?) ?: ""
            val serviceId = (ser["serviceId"] as Long?) ?: 0L
            val cost = (ser["cost"] as Double?) ?: 0.0
            val service  = Service(description, serviceId, cost)
            services.add(service)
        }
        return services
    }
}