import static Color.colorize

List<DnsAliasSwitch> aliasSwitches = [
    // configure here
    new DnsAliasSwitch("example", "1.1.1.1", "2.2.2.2"),
]

println "-------------------------------------------------------"
println "Alias-Name   Old-IP         New-IP         Switched    "
println "-------------------------------------------------------"
aliasSwitches.each { DnsAliasSwitch aliasSwitch ->
    println "${aliasSwitch.aliasName.padRight(12)} ${aliasSwitch.oldIp.padRight(14)} ${aliasSwitch.newIp.padRight(14)} ${check(aliasSwitch)}"
}

static String check(DnsAliasSwitch aliasSwitch) {
    try {
        InetAddress dnsAddress = InetAddress.getByName aliasSwitch.aliasName
        switch (dnsAddress.hostAddress) {
            case aliasSwitch.oldIp:
                return colorize('NO', Color.YELLOW)
                break
            case aliasSwitch.newIp:
                return colorize('YES', Color.GREEN)
                break
            default:
                return colorize("BAD IP ($dnsAddress.hostAddress)", Color.RED)
        }
    } catch (UnknownHostException ignored) {
        return colorize('UNKNOWN HOST', Color.RED)
    }
}

class DnsAliasSwitch {
    final String aliasName
    final String oldIp
    final String newIp

    DnsAliasSwitch(String aliasName, String oldIp, String newIp) {
        this.aliasName = aliasName
        this.oldIp = oldIp
        this.newIp = newIp
    }
}

enum Color {
    RED('31'),
    YELLOW('33'),
    GREEN('32');

    private Color(String colorCode) {
        this.colorCode = colorCode
    }

    final String colorCode

    static String colorize(String str, Color color) {
        char esc = 27 as char
        return "$esc[${color.colorCode}m$str$esc[39m"
    }
}
