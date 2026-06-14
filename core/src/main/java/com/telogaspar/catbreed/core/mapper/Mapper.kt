package com.telogaspar.catbreed.core.mapper

interface Mapper<S, T> {
    fun map(source: S): T
}