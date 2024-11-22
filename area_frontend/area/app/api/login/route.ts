import { NextRequest } from "next/server";

export async function POST(request: NextRequest) {
    const body = await request.json()

    console.log(body);
    console.log(request.headers.get('Content-Type'))

    return new Response('OK')
}