
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
 # creates a new Reflectivity Model and shows how to customize and build up
 # the layers in the model from scratch. 
 # ****************************************************************************

# Needed imports from ICE
from org.eclipse.ice.datastructures.form import Material

# Load the Platform module for accessing OSGi services
loadModule('/System/Platform')

# Get the core service from ICE for creating and accessing objects. 
coreService = getService(org.eclipse.ice.core.iCore.ICore);

# Create the reflectivity model to be used and get its reference. The create item 
# method will return a string representing the number of that item, so use int() to 
# convert it to an integer. 
reflectModel = coreService.getItem(int(coreService.createItem("Reflectivity Model")))

# Gets the list component used as the data for the table (is on tab 2)
listComp = reflectModel.getComponent(2)

# Now we want to build up the list from our own data, so we can do that here.

# The first step would be to clear the list so that we can start adding to it. Clearing
# the list requires the locks as multiple operations are happening and we need to 
# protect the list from multiple threads trying to access it at the same time. 
listComp.getReadWriteLock().writeLock().lock()
listComp.clear()
listComp.getReadWriteLock().writeLock().unlock()

# Create the layer of air
air = Material()
air.setName("Air")
air.setProperty("Material ID", 1)
air.setProperty("Thickness (A)", 200)
air.setProperty("Roughness (A)", 0)
air.setProperty(Material.SCAT_LENGTH_DENSITY, 0)
air.setProperty(Material.MASS_ABS_COHERENT, 0)
air.setProperty(Material.MASS_ABS_INCOHERENT, 0)

# Create the Aluminum Oxide layer
AlOx = Material()
AlOx.setName("AlOx")
AlOx.setProperty("Material ID", 2)
AlOx.setProperty("Thickness (A)", 25)
AlOx.setProperty("Roughness (A)", 10.2)
AlOx.setProperty(Material.SCAT_LENGTH_DENSITY, 1.436e-6)
AlOx.setProperty(Material.MASS_ABS_COHERENT, 6.125e-11)
AlOx.setProperty(Material.MASS_ABS_INCOHERENT, 4.47e-12)

# Create the Aluminum layer
Al = Material()
Al.setName("Al")
Al.setProperty("Material ID", 3)
Al.setProperty("Thickness (A)", 500)
Al.setProperty("Roughness (A)", 11.4)
Al.setProperty(Material.SCAT_LENGTH_DENSITY, 2.078e-6)
Al.setProperty(Material.MASS_ABS_COHERENT, 2.87e-13)
Al.setProperty(Material.MASS_ABS_INCOHERENT, 1.83e-12)

# Create the Aluminum Silicate layer
AlSiOx = Material()
AlSiOx.setName("AlSiOx")
AlSiOx.setProperty("Material ID", 4)
AlSiOx.setProperty("Thickness (A)", 10)
AlSiOx.setProperty("Roughness (A)", 17.2)
AlSiOx.setProperty(Material.SCAT_LENGTH_DENSITY, 1.489e-6)
AlSiOx.setProperty(Material.MASS_ABS_COHERENT, 8.609e-9)
AlSiOx.setProperty(Material.MASS_ABS_INCOHERENT, 6.307e-10)

# Create the Silicon layer
Si = Material()
Si.setName("Si")
Si.setProperty("Material ID", 5)
Si.setProperty("Thickness (A)", 100)
Si.setProperty("Roughness (A)", 17.5)
Si.setProperty(Material.SCAT_LENGTH_DENSITY, 2.07e-6)
Si.setProperty(Material.MASS_ABS_COHERENT, 4.7498e-11)
Si.setProperty(Material.MASS_ABS_INCOHERENT, 1.9977e-12)

# Add all of the materials back to the list (in top to bottom order)
listComp.getReadWriteLock().writeLock().lock()
listComp.add(air);
listComp.add(AlOx);
listComp.add(Al);
listComp.add(AlSiOx);
listComp.add(Si);
listComp.getReadWriteLock().writeLock().unlock()

# Finally process the model to get the results. 
coreService.processItem(reflectModel.getId(), "Calculate Reflectivity", 1);



"""*****BEGIN SIGNSTURE********
MCwCFEEypAyYSRAOoS4hftOE0j4bBoMGAhQhsA7Umn/cl854cG+2GDEzJv1pyQ==

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