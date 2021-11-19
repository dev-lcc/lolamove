package hk.com.lolamove.datasource.remote.dto

interface Mapper<I, O> {
    fun map(input: I): O
}