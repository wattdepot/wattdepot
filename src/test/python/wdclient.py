"""Test program using the Wattdepot Python client library wattdepot.py. """

import wattdepot
import datetime

wd = wattdepot.WattdepotClient("http://localhost:8192", "uh", "yxu", "yxu")

# display sensors and depositories info
print wd.get_sensors()
print wd.get_depositories()

# create a sensor
wd.create_sensor("yxu_sensor","yxu", "http://example.com", "shark")

# put a measurement into wattdepot
today = datetime.datetime.today()
depository=wd.get_depository("energy")
units=depository.measurementType.units
wd.put_measurement("energy", "yxu_sensor", today, 105.1, units)

# retrieve the latest measurement value
print wd.get_latest_value("energy", "yxu_sensor")

# retrieve measurements
start = today - datetime.timedelta(days=1)
end = today
print wd.get_measurements("energy", "yxu_sensor", start, end)

