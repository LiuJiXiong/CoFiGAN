# CoFiGAN: Collaborative Filtering by Generative and Discriminative Training for One-Class Recommendation.
Authors: Jixiong Liu, Weike Pan and Zhong Ming.

## Description
We make personalized recommendation via the Generative Adversarial Network. In our model, the discriminator can not only discriminate the generated samples from the ground truth, but also provide positive and negative samples that are viewed as the goal of the generator, and the generator generate samples via the guidance of the discriminator. Compared to other recommendation models based on GANs, our model shows strong performances on four different datasets.

## Pre train
- Generator: BPR-MF
- Discriminator: Logistic-MF

## Datasets
There are four datasets in the experiment, including MovieLens-100K, MovieLens-1M, UserTag, Netflix5K5K, all can be downloaded from the [site](http://csse.szu.edu.cn/staff/panwk/publications/cofigan/ ).

## Baselines
- BPR-MF
- Logistic-MF
- IRGAN

## How to run?
We take experiment on ML100K as the example:
1. Compile: javac *.java
2. Run: java Main -n 943 -m 1682 -d 20 -g_alpha_u 0.001 -g_alpha_v 0.001 -g_beta_v 0.001 -g_gamma 0.01 -d_alpha_u 0.01 -d_alpha_v 0.01 -d_beta_v 0.01 -d_gamma 0.01 -fnTrainData ../dataset/ML100K-copy1-train -fnTestData ../dataset/ML100K-copy1-test -num_iterations 300 -topK 10 -d_epoch 50 -g_epoch 50 -batchsize 32 -g_fnInputModel ../pre-train/copy1/ml100k/G_pre_model-20.model -d_fnInputModel ../pre-train/copy1/ml100k/D_pre_model-20.model


Last Updated: November 5, 2019.