package com.davjhan.hangdx

import ktx.inject.Context

/**
 * Created by david on 2018-02-05.
 */
open class HApp (val context:Context){
    val disp:Disp = context.inject()

}
