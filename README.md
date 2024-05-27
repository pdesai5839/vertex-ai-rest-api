# vertex-ai-rest-api

Google Cloud Vertex AI, is a managed machine learning (ML) platform that developers, data scientists, and researchers use to build, deploy, and manage high-quality, scalable ML models. Vertex AI makes it easy to build, test, deploy, and manage AI models.

This project demonstrates how to call Google Vertex AI via REST API in Java.

Requires Google OAuth2 for authentication with Google services.
Since the default authentication token expires in 1 hour, create a service account in GCP. This allows you to generate a key file containing all the information needed for creating and refreshing the authentication token. With this approach, you can extend the token lifetime to 12 hours.

Java 21 required!

This project will train Vertex AI to create a very simple personal financial advisor with a knack for answering in California Surfer style (because, why not!).

Some sample input/output:

Input: `I an heard emergency fund is the way to go. Do you agree?`

Output: `Totally dude! An emergency fund is like your safety net, ya know? It's that extra cash you stash away for unexpected stuff, like a flat tire or a surprise medical bill. It's gonna keep you chill when life throws you a curveball. Think of it as your financial surfboard, keeping you from wiping out during rough financial waves. Just gotta make sure you're putting some aside, even if it's just a little bit each month. It's all about being prepared, brah.`

Input: `I think my home insurance is too expensive. Is it a good idea to shop around for cheaper insurance? I'm worried about penalties.`

Output: `Totally get you, brah! Shopping around for a better insurance deal is like scoring a sweet wave. It's all about finding the best price without getting ripped off. No penalties for switching, but double-check with your current insurer to see if there are any cancellation fees. Then, compare apples to apples - look at coverage limits, deductibles, and the fine print to make sure you're getting the best value.`
