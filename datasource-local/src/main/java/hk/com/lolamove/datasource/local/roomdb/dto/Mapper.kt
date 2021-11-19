package hk.com.lolamove.datasource.local.roomdb.dto

interface Mapper<I, O> {
    fun map(input: I): O
}