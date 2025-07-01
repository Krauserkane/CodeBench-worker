# CodeBench- Worker
![image](https://github.com/user-attachments/assets/6c9bc9d5-4b50-4b19-9dbc-59bfb526f1a7)


# CodeBench Worker

The **CodeBench Worker** is the execution engine of the CodeBench system.  
It consumes code submissions from a Kafka topic, runs each submission securely inside a sandboxed environment, and stores the execution result in a MySql DB for the CodeBench-server to pick up and deliver to the user.

## ⚙️ How It Works

1. The server pushes user-submitted code to a Kafka topic.
2. The worker listens for new submissions.
3. Each submission is executed inside a sandbox (e.g., using Isolate).
4. The result of the Code Execution is Stored inside a MySql Db which is picked up by the server when a submissionId is queried.
