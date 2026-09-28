import { createServer } from "node:http";
import { readFile } from "node:fs/promises";
import { extname, join, normalize } from "node:path";
import { fileURLToPath } from "node:url";

const host = "127.0.0.1";
const port = Number(process.env.PORT || 5500);
const root = fileURLToPath(new URL("./dist/front-cadastro-produto/browser/", import.meta.url));
const mediaTypes = {
    ".css": "text/css; charset=utf-8",
    ".html": "text/html; charset=utf-8",
    ".js": "text/javascript; charset=utf-8",
    ".json": "application/json; charset=utf-8",
    ".svg": "image/svg+xml",
};

const server = createServer(async (request, response) => {
    try {
        const requestUrl = new URL(request.url || "/", `http://${request.headers.host || host}`);
        const relativePath = requestUrl.pathname === "/"
            ? "index.html"
            : normalize(decodeURIComponent(requestUrl.pathname)).replace(/^[/\\]+/, "");
        const filePath = join(root, relativePath);

        if (!filePath.startsWith(root)) {
            response.writeHead(403).end("Acesso negado");
            return;
        }

        let content;
        let responsePath = filePath;
        try {
            content = await readFile(responsePath);
        } catch (error) {
            if (extname(relativePath)) throw error;
            responsePath = join(root, "index.html");
            content = await readFile(responsePath);
        }
        response.writeHead(200, {
            "Content-Type": mediaTypes[extname(responsePath).toLowerCase()] || "application/octet-stream",
            "Cache-Control": "no-store",
            "X-Content-Type-Options": "nosniff",
        });
        response.end(content);
    } catch (_error) {
        response.writeHead(404, { "Content-Type": "text/plain; charset=utf-8" });
        response.end("Arquivo não encontrado");
    }
});

server.listen(port, host, () => {
    console.log(`Front-end disponível em http://${host}:${port}`);
});
