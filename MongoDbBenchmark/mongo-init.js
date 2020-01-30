db.createUser(
        {
            user: "benchmark",
            pwd: "benchmark",
            roles: [
                {
                    role: "readWrite",
                    db: "benchmark"
                }
            ]
        }
);
