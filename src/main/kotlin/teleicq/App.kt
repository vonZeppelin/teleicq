package teleicq

import teleicq.lowlevel.PurpleFacade

fun main(args: Array<String>) {
    PurpleFacade.init("teleicq", debug = true)
    println(PurpleFacade.protocols)
    PurpleFacade.quit()
}
