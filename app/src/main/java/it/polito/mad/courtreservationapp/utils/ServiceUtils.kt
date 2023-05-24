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

    fun getService(serviceId: Long): Service{
        return services[serviceId] ?: Service("Error", -1L)
    }

    fun getServices(serviceIdList: List<Long>): List<Service>{
        val servicesList = mutableListOf<Service>()
        serviceIdList.forEach{
            servicesList.add(getService(it))
        }
        return servicesList
    }
}