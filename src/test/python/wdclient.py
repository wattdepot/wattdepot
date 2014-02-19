"""Test program using the Wattdepot Python client library wattdepot.py. """

import wattdepot
import datetime

wd = wattdepot.WattdepotClient(server_uri="http://localhost:8192", 
                               orgid="uh", 
                               username="yxu", 
                               password="yxu")

# display sensors and depositories info
print wd.get_sensors()
print wd.get_depositories()

# create a sensor
wd.create_sensor(id="my_sensor", 
                 name="my sensor", 
                 uri="http://example.com", 
                 modelId="shark")
print wd.get_sensor("my_sensor").__dict__

# create a depository
wd.create_depository(id="my_depot", 
                     name="my depot", 
                     measurement_type_id="energy-wh")
print wd.get_depository("my_depot").__dict__

# put a measurement into wattdepot
today = datetime.datetime.today()
depository=wd.get_depository("my_depot")
units=depository.measurementType.units
wd.put_measurement("my_depot", "my_sensor", today, 105.1, units)

# retrieve the latest measurement value
print wd.get_latest_value("my_depot", "my_sensor")

# retrieve measurements
start = today - datetime.timedelta(days=1)
end = today
print wd.get_measurements("my_depot", "my_sensor", start, end)

