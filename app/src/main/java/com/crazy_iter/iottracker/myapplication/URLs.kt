package com.crazy_iter.iottracker.myapplication

class URLs {

    companion object {
        var base = "http://iottracker.warshe.net/api/"
        var account = base + "account/"
        var login = account + "login/"
        var realTime = base + "RealTime/Get/?AccountID="
    }

}