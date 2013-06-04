#!/usr/bin/gawk -f

/"free-memory"/ {
    match($0, /free-memory"\s*:\s*([0-9]+)/, a)
    free_memory = a[1] + 0
}

/"total-memory"/ {
    match($0, /total-memory"\s*:\s*([0-9]+)/, a)
    total_memory = a[1] + 0
}

END {
    print total_memory - free_memory
}
