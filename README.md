# meta-bolt-wayland-egl-test

A **template** bitbake meta layer for building a Firebolt **bolt** application.

This layer is not meant to be built as-is. It is a *wayland-egl-test*: a minimal,
working starting point that you copy into your **own repository** and rename to
your application. It builds a trivial "hello world" bolt app
(`recipes-extended/wayland-egl-test/files/wayland-egl-test.cpp`) so that, once renamed, you have
an end-to-end OCI image and bolt package that already build and run — ready to
be filled in with your real application.

Unlike [`meta-bolt-app-examples`](https://github.com/rdkcentral/meta-bolt-distro/tree/develop/meta-bolt-app-examples),
which lives inside the distro repository, this layer is **standalone**: it ships
its own `setup-environment`, `repo-sync` and `manifests/deps.xml`, which fetch
[`meta-bolt-distro`](https://github.com/rdkcentral/meta-bolt-distro) into
`deps/bolt` and wire up the build. That makes it suitable as the seed of an
external bolt-application repository.

## Creating your application from this wayland-egl-test

Create a new repository for your application, named `meta-bolt-<app>` (e.g.
`meta-bolt-myapp`), and copy the **contents** of this wayland-egl-test into its root —
the new repository *is* the layer, there is no nested `meta-bolt-wayland-egl-test` or
`meta-bolt-<app>` directory inside it.

Then replace every occurrence of `wayland-egl-test` with your application name. The name
is used in directory names, file names, the recipe, the package id, the image
and the layer collection name, so a search-and-replace plus a few renames is all
that is needed.

The following helper does this for an app called `myapp` — run it from the
repository root, while it still contains only the wayland-egl-test files:

```bash
APP=myapp

# rename files and directories
mv recipes-extended/wayland-egl-test                 recipes-extended/${APP}
mv recipes-extended/${APP}/wayland-egl-test.bb       recipes-extended/${APP}/${APP}.bb
mv recipes-extended/${APP}/files/wayland-egl-test.cpp recipes-extended/${APP}/files/${APP}.cpp
mv recipes-core/images/wayland-egl-test-bolt-image.bb recipes-core/images/${APP}-bolt-image.bb
mv package-configs/com.rdkcentral.wayland-egl-test.json package-configs/com.rdkcentral.${APP}.json
mv package-configs/wayland-egl-test.bolt.json           package-configs/${APP}.bolt.json

# replace remaining string occurrences (layer name, recipe contents, configs, ...)
grep -rIl wayland-egl-test . | xargs sed -i "s/wayland-egl-test/${APP}/g"
```

After running it, review the result — in particular
`package-configs/com.rdkcentral.${APP}.json` (id, name, entry point,
permissions, configuration) and `conf/layer.conf`
(`BBFILE_COLLECTIONS`/`LAYERDEPENDS`). Fill in the `NOTICE` file by replacing
the `<YEAR>` and `<COPYRIGHT HOLDER>` placeholders with your copyright
information. Finally, replace this README with one describing your
application.

## Repository layout

```
conf/                         layer.conf and conf-notes for this meta layer
manifests/deps.xml            repo manifest of build dependencies (meta-bolt-distro)
package-configs/              bolt package metadata (.bolt.json + com.rdkcentral.<app>.json)
recipes-core/images/          the *-bolt-image image recipe
recipes-extended/<app>/       your application recipe and sources
setup-environment, repo-sync  standalone build harness
```

## Setup and building

Build-host prerequisites are the same as for the distro — see
[Setup and building](https://github.com/rdkcentral/meta-bolt-distro/blob/develop/README.md#setup-and-building)
in the [meta-bolt-distro](https://github.com/rdkcentral/meta-bolt-distro)
documentation.

* Clone your application repository and enter its root directory (which is the
  layer root).
```
git clone <your-app-repo-url>
cd meta-bolt-<app>
```

* Set up the build environment. This fetches `meta-bolt-distro` into `deps/bolt`
  (via `repo-sync`) and configures the build.
```
source setup-environment
```

## Building the application as a bolt package

This is the standard way of building the application. The
[bolt tool](https://github.com/rdkcentral/bolt-tools/tree/main/bolt)'s
[bolt make](https://github.com/rdkcentral/bolt-tools/blob/main/bolt/docs/make.md)
command builds the OCI image itself as part of creating the package, so there
is no need to build it separately beforehand.

The bolt tool and the tools it depends on do not have to be preinstalled on the
build host — if any of them are missing, a single command compiles them all and
sets up the bolt tool, making them available on the PATH of the build
environment:
```
bitbake bolt-env
```

With the build environment set up (`source setup-environment`, see above),
ensure the base package is available in the
[local package store](https://github.com/rdkcentral/bolt-tools/blob/main/bolt/docs/local-package-store.md) —
either download it
with [bolt fetch](https://github.com/rdkcentral/bolt-tools/blob/main/bolt/docs/fetch.md)
or build it yourself as described in
[Building the base bolt package](https://github.com/rdkcentral/meta-bolt-distro?tab=readme-ov-file#building-the-base-bolt-package) —
then run:
```
bolt make wayland-egl-test
```

## Building only the OCI image

If you want the OCI image without creating a bolt package, build it directly
with bitbake (also requires the build environment to be set up):
```
bitbake wayland-egl-test-bolt-image             # no multi config

bitbake mc:arm:wayland-egl-test-bolt-image   \
        mc:arm64:wayland-egl-test-bolt-image \
        mc:amd64:wayland-egl-test-bolt-image    # requires multi config
```

## Running the bolt package on a device

Use `bolt push` and `bolt run` as described in their help pages —
[bolt push](https://github.com/rdkcentral/bolt-tools/blob/main/bolt/docs/push.md) and
[bolt run](https://github.com/rdkcentral/bolt-tools/blob/main/bolt/docs/run.md).
`<remote>` is the hostname or alias of a device reachable over SSH in
non-interactive mode.
```
bolt push <remote> com.rdkcentral.base+0.2.0
bolt push <remote> com.rdkcentral.wayland-egl-test+*

bolt run  <remote> com.rdkcentral.wayland-egl-test+*
```

## Adding more functionality

The wayland-egl-test intentionally does the bare minimum. For richer, real-world
recipes — graphics (Wayland/EGL), input handling, third-party libraries, and so
on — copy the recipes you need from
[`meta-bolt-app-examples`](https://github.com/rdkcentral/meta-bolt-distro/tree/develop/meta-bolt-app-examples)
into this layer and add their outputs to your image's `IMAGE_INSTALL`.

## Pinning dependencies

`manifests/deps.xml` controls which version of `meta-bolt-distro` (and any other
repositories you add) is fetched. `revision` must be a **tag**
(`refs/tags/<tag>`) or a **commit SHA**, never a branch, to keep builds
reproducible. For releases, use the distro release tag matching the base layer
version the app depends on (see `dependencies` in
`package-configs/com.rdkcentral.wayland-egl-test.json`), for example:
```xml
<project remote="rdkcentral" name="meta-bolt-distro"
         upstream="main" revision="refs/tags/0.2.0" path="bolt"/>
```
During development, a revision of a development version may be used as well,
for example a commit SHA on the `develop` branch:
```xml
<project remote="rdkcentral" name="meta-bolt-distro"
         upstream="develop" revision="<commit SHA>" path="bolt"/>
```
Add any extra meta-layer repositories your recipes need as further `<project>`
entries.
