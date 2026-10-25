FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

RUN npm run build && \
mkdir -p /app/dist/production && \
    if [ -d /app/dist/frontend-service/browser ]; then \
      cp -R /app/dist/frontend-service/browser/. /app/dist/production/; \
    else \
      cp -R /app/dist/frontend-service/. /app/dist/production/; \
    fi


FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/production /usr/share/nginx/html

EXPOSE 80
