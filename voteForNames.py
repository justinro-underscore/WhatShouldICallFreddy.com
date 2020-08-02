import requests
import math
import random
import time

def vote():
  base_url = "http://localhost:8080"
  dog_names = requests.get(base_url + "/dognames").json()
  dog_names = dog_names["_embedded"]["dogNameList"]
  dog_name_indeces = list(map(lambda dog : dog["id"], dog_names))

  user = requests.post(base_url + "/user").json()

  num_times_to_vote = 50
  for _ in range(num_times_to_vote):
    vote_yes = bool(math.floor(random.random() * 2))
    index = math.floor(random.random() * len(dog_name_indeces))
    dog_id = dog_name_indeces[index]
    print("Voting " + ("yes" if vote_yes else "no") + " for id " + str(dog_id))
    res = requests.post(base_url + "/dognames/vote/" + str(dog_id) + "/" + ("true" if vote_yes else "false"), cookies={"user": str(user)})
    print("Success!" if res.status_code else "Failure :(")
    time.sleep(0.2)

vote()