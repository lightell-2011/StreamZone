# StreamZone — free streaming starter

## Requirements
- Windows 10/11
- Node.js LTS (free): https://nodejs.org/
- Chrome or Edge

## Run
1. Install Node.js LTS.
2. Extract this ZIP.
3. Open the extracted `StreamZone` folder in File Explorer.
4. Click the address bar, type `cmd`, press Enter.
5. Run `npm install`
6. Run `npm start`
7. Open http://localhost:3000 in Chrome or Edge.

Keep the terminal open. Press Ctrl+C to stop the server.

## Test
1. In one browser window, click Creator Studio, enter a channel/title, and allow camera and microphone.
2. Open a second browser window at http://localhost:3000.
3. Click the live channel and test chat/video.

## Important limits
This is a real starter implementation, not a full Twitch replacement. It uses WebRTC peer-to-peer video and Socket.IO signaling/chat. It is suitable for learning and small local tests, but some network configurations prevent direct video connections, and mesh video does not scale to large audiences. Public deployment, HTTPS, persistent accounts, moderation, and scalable media infrastructure are later steps. Live channel data resets when the server restarts. There are no paid dependencies, but public hosting and high-volume video bandwidth cannot be guaranteed free.
