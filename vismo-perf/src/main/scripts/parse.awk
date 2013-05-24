#!/usr/bin/gawk -f

/^latency:/ {
    sub(/^latency: /, "")
    printf "%s,", extract_mean($0)
}

/throughput/ {
    sub(/^throughput: /, "")
    printf "%s\n", extract_mean($0)
}

function extract_mean(line,     n, a, b, i) {
    n = split(line, a, /, /)

#    for (i = 1; i <= n; ++i)
#        print a[i]

    split(a[2], b, "=")

    return b[2]
}
