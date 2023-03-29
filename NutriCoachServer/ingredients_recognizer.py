import torch
from pip._internal.utils import models
from torch import nn


class IngredientsRecognizer:

    def __init__(self):
        pass
        ''''
        model_path = './models/model_e500_v-8.950.pth.tar'
        resnet = models.resnet50(pretrained=True)
        modules = list(resnet.children())[:-1]  # we do not use the last fc layer.
        self.visionMLP = nn.Sequential(*modules)

        self.visual_embedding = nn.Sequential(
            nn.Linear(opts.imfeatDim, opts.embDim),
            nn.Tanh(),
        )

        self.recipe_embedding = nn.Sequential(
            nn.Linear(opts.irnnDim * 2 + opts.srnnDim, opts.embDim, opts.embDim),
            nn.Tanh(),
        )

        self.model = model = im2recipe()

        torch.load(model_path, encoding='latin1', map_location='cpu')

    def predict(self, image):
        return self.model.predict(image)


if __name__ == '__main__':
    rec = IngredientsRecognizer()
    print(rec)
    print(rec.model.modules())
    '''
