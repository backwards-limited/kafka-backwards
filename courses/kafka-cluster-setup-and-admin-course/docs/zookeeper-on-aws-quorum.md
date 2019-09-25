# Zookeeper Quorum Setup

Goals:

- Create an AMI (image) from the existing machine
- Create other 2 machines and launch Zookeeper on them
- Test that the Quorum is running and working

![Creating AMI image](images/creating-ami-image.png)

---

![Create AMI image](images/create-ami-image.png)

---

![View AMI images](images/view-ami-images.png)

---

![AMI snapshot image](images/ami-snapshot-image.png)

---

![AMI image](images/ami-image.png)

---

![Instance type from AMI](images/instance-type-from-ami.png)

This time we choose **eu-west-2b** which has IP **172.31.16.0**, and from this we can set a **primary IP** within **network interfaces** as say **172.31.19.230**:

![AMI configure instance](images/ami-configure-instance.png)

---

![AMI tag](images/ami-tag.png)

---

![AMI security group](images/ami-security-group.png)

---

![Two EC2 instances](images/two-ec2-instances.png)

And let's launch our third instance with IP of **172.31.35.20**:

![AMI image](images/ami-image.png)

---

![Third configure instance](images/third-configure-instance.png)

---

![Server 3 tag](images/server-3-tag.png)

---

![3 instances](images/three-instances.png)

## SSH onto the three instances

![Three shells](images/three-shells.png)

---

![SSH logged in](images/ssh-logged-in.png)