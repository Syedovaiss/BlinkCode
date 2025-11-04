package com.ovais.blinkcode.utils.usecase

fun interface ParameterizedUseCase<InputType, ReturnType> {
    suspend operator fun invoke(input: InputType): ReturnType
}

fun interface UseCase<ReturnType> {
    operator fun invoke(): ReturnType
}

