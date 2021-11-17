package hk.com.lolamove.datasource.remote.dto.mapper

import hk.com.lolamove.domain.datamodels.Sender
import hk.com.lolamove.datasource.remote.dto.DTOSender
import hk.com.lolamove.datasource.remote.dto.Mapper

object SenderMapper: Mapper<DTOSender, Sender> {
    override fun map(input: DTOSender): Sender =
        Sender(
            phone = input.phone,
            name = input.name,
            email = input.email,
        )
}