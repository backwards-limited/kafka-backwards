# Setup EC2 instance

Once you've logged into your AWS account (registering if necessary):

![Logged into AWS](images/logged-into-aws.png)

and let's first navigate to **VPC**:

![VPC](images/vpc.png)

and then take a look at **subnets** which basically shows availability zones:

![Subnets](images/subnets.png)

---

![EC2](images/ec2.png)

Create an EC2 instance:

![Ubuntu](images/ubuntu-ec2.png)

---

![Choose instance type](images/choose-instance-type.png)

Choose subnet and add an IP in the range of that subnet e.g. I first choose **eu-west-2a** which has IP **172.31.0.0** (as noted from the screenshot above), and from this we can set a **primary IP** within **network interfaces** as say **172.31.9.1**:

![Primary IP](images/primary-ip.png)

---

![Name tag](images/name-tag.png)

---

![Security group](images/security-group.png)

---

![Key pair](images/key-pair.png)

---

![Instance launched](images/instance-launched.png)

---

![Instance details](images/instance-details.png)

If you wish to keep the private IP, do the following:

![Saving private IP](images/saving-private-ip.png)

---

![Changing termination behaviour](images/changing-termination-behaviour.png)

---

![Untick](images/delete-on-termination.png)