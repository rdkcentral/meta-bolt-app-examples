#!/bin/sh

# If not stated otherwise in this file or this component's LICENSE file the
# following copyright and licenses apply:

# Copyright 2026 RDK Management

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

# http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

#May need it while bolt run --direct
export WAYLAND_DISPLAY="${WAYLAND_DISPLAY:-wayland-0}"
export XDG_RUNTIME_DIR="${XDG_RUNTIME_DIR:-/run/user/0}"
WAYLAND_SOCKET_PATH="${XDG_RUNTIME_DIR}/${WAYLAND_DISPLAY}"

#Uncomment to enable wayland logs
#WAYLAND_DEBUG=1

#provide all incase vulkan loader debug logs need to be enabled,its unset by default
export VK_LOADER_DEBUG="${VK_LOADER_DEBUG:-}"

VKMARK_WINSYS_DIR="/usr/lib/vkmark"
run_result=0
ran_any=0

VKMARK_SIZE="${VKMARK_SIZE:-1920x1080}"

run_vkmark() {
    backend=$1
    shift
    time vkmark --winsys "$backend" --winsys-dir="${VKMARK_WINSYS_DIR}" --size "${VKMARK_SIZE}" "$@"
}

run_all_tests() {
    run_vkmark "$1"
    return $?
}

if [ -f "$VKMARK_WINSYS_DIR/wayland.so" ]; then
    if [ -S "$WAYLAND_SOCKET_PATH" ]; then
        echo "=== vkmark: wayland start ==="

        if run_all_tests wayland; then
            rc=0
        else
            rc=$?
        fi

        [ "$rc" -ne 0 ] && run_result=1
        echo "=== vkmark: wayland end ==="
        ran_any=1
    else
        echo "vkmark-run: wayland plugin present but no Wayland socket at ${WAYLAND_SOCKET_PATH}, skipping"
    fi
fi

if [ -f "$VKMARK_WINSYS_DIR/xcb.so" ]; then
    if [ -n "$DISPLAY" ]; then
        echo "=== vkmark: xcb start ==="

        if run_all_tests xcb; then
            rc=0
        else
            rc=$?
        fi

        [ "$rc" -ne 0 ] && run_result=1
        echo "=== vkmark: xcb end ==="
        ran_any=1
    else
        echo "vkmark-run: xcb plugin present but DISPLAY not set, skipping"
    fi
fi

if [ "$ran_any" -eq 0 ]; then
    echo "vkmark-run: no usable window system found, exiting"
    exit 1
fi

exit $run_result
