import { pathToFileURL, fileURLToPath } from "node:url"
import path from "node:path"
import fs from "node:fs"

const here = path.dirname(fileURLToPath(import.meta.url))
const entry = path.join(here, "svg-render/node_modules/@resvg/resvg-js/index.js")
const { Resvg } = await import(pathToFileURL(entry).href)

const [src, dst, width] = process.argv.slice(2)
const svg = fs.readFileSync(src)
const rendered = new Resvg(svg, {
  fitTo: { mode: "width", value: Number(width) },
  background: "rgba(0,0,0,0)",
}).render()
fs.writeFileSync(dst, rendered.asPng())
