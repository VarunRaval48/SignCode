
 # ****************************************************************************
 # Copyright (c) 2015 UT-Battelle, LLC.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 #
 # Contributors:
 #   Initial API and implementation and/or initial documentation - Kasper
 # Gammeltoft, Jay Jay Billings
 #
 # This is an example script designed to show how to use ease with ICE. It 
 # creates a new Reflectivity Model and processes it, using the default mock
 # data and inputs. 
 # ****************************************************************************

# Load the Platform module for accessing OSGi services
loadModule('/System/Platform')

# Get the core service from ICE for creating and accessing objects. 
coreService = getService(org.eclipse.ice.core.iCore.ICore);

# Create the reflectivity model to be used and get its reference. The create item 
# method will return a string representing the number of that item, so use int() to 
# convert it to an integer. 
reflectModel = coreService.getItem(int(coreService.createItem("Reflectivity Model")))

# This is usually where you would do your own customization and automation regarding
# the reflectivity model you just created. Maybe change the layers, or do some custom
# calculations. 


# Finally process the model to get the results. 
coreService.processItem(reflectModel.getId(), "Calculate Reflectivity", 1);


"""*****BEGIN SIGNSTURE********
MCwCFGtDjmqyeycVHOMvgMcn/sfRCjolAhQnwBAxTKctzxE7C4JN1wmkFhY90Q==

MIIDNzCCAvWgAwIBAgIELNrJgDALBgcqhkjOOAQDBQAwbTEL
MAkGA1UEBhMCSU4xEDAOBgNVBAgTB0d1amFyYXQxEjAQBgNV
BAcTCUFobWVkYWJhZDEQMA4GA1UEChMHVW5rbm93bjEQMA4G
A1UECxMHVW5rbm93bjEUMBIGA1UEAxMLVmFydW4gUmF2YWww
HhcNMTYwMzA1MTE1NjUwWhcNMTYwNjAzMTE1NjUwWjBtMQsw
CQYDVQQGEwJJTjEQMA4GA1UECBMHR3VqYXJhdDESMBAGA1UE
BxMJQWhtZWRhYmFkMRAwDgYDVQQKEwdVbmtub3duMRAwDgYD
VQQLEwdVbmtub3duMRQwEgYDVQQDEwtWYXJ1biBSYXZhbDCC
AbgwggEsBgcqhkjOOAQBMIIBHwKBgQD9f1OBHXUSKVLfSpwu
7OTn9hG3UjzvRADDHj+AtlEmaUVdQCJR+1k9jVj6v8X1ujD2
y5tVbNeBO4AdNG/yZmC3a5lQpaSfn+gEexAiwk+7qdf+t8Yb
+DtX58aophUPBPuD9tPFHsMCNVQTWhaRMvZ1864rYdcq7/Ii
Axmd0UgBxwIVAJdgUI8VIwvMspK5gqLrhAvwWBz1AoGBAPfh
oIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrU
WU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCj
rh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4Vrl
nwaSi2ZegHtVJWQBTDv+z0kqA4GFAAKBgQDqLrJZitkj0fqO
RQ/kdKtwHK4Fq6kXfGedp5umydmCVqrIkuCKuw6X2P5gX4Vv
0kqTEG2iWL7Hv3iUCmtaCeKYLSlIyaloJMYPwgcKxWYYMtXn
njfoOAxHywwXxPAygkR/r9TH1VrUSKjvuGvOxdjSNnezjsVL
VEyIXiO76ZfawKMhMB8wHQYDVR0OBBYEFJw/5/p+5vXMZPXx
ZLBh9YLK/zr4MAsGByqGSM44BAMFAAMvADAsAhRA44+6n9Ya
UTnckDGsbZIv450sVAIUA1otxObPsQaTs1EcOEEqODrNHCY=

********END SIGNSTURE*****"""